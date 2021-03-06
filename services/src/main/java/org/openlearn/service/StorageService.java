package org.openlearn.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.*;
import com.google.common.collect.Lists;
import org.openlearn.client.S3Client;
import org.openlearn.config.ApplicationProperties;
import org.openlearn.domain.*;
import org.openlearn.dto.FileInformationDTO;
import org.openlearn.repository.AssignmentRepository;
import org.openlearn.repository.CourseRepository;
import org.openlearn.repository.FileRepository;
import org.openlearn.repository.PortfolioItemRepository;
import org.openlearn.security.AuthoritiesConstants;
import org.openlearn.security.SecurityUtils;
import org.openlearn.transformer.FileInformationTransformer;
import org.openlearn.web.rest.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Service Implementation for managing File upload.
 */
@Service
@Transactional
@EnableConfigurationProperties
public class StorageService {
	private static final Logger log = LoggerFactory.getLogger(StorageService.class);

	private final FileRepository fileRepository;

	private final CourseRepository courseRepository;

	private final AssignmentRepository assignmentRepository;

	private final UserService userService;

	private final PortfolioItemRepository portfolioItemRepository;

	private final FileInformationTransformer fileInformationTransformer;

	@Autowired
	private ApplicationProperties props;

	public StorageService(final FileRepository fileRepository,
							final CourseRepository courseRepository,
							final AssignmentRepository assignmentRepository,
							final UserService userService,
							final PortfolioItemRepository portfolioItemRepository,
							final FileInformationTransformer fileInformationTransformer) {
		this.fileRepository = fileRepository;
		this.courseRepository = courseRepository;
		this.assignmentRepository = assignmentRepository;
		this.userService = userService;
		this.portfolioItemRepository = portfolioItemRepository;
		this.fileInformationTransformer = fileInformationTransformer;
	}

	/**
	 * Store a file.
	 *
	 * @return the persisted entity
	 */
	public FileInformationDTO store(final MultipartFile file, Long assignmentId, Long portfolioId) {
		log.debug("Request to save f : {}", file); //TODO cbernal fix this log statement
		Assignment assignment = null;
		PortfolioItem portfolioItem = null;
		User uploadedBy = null;

		if (assignmentId != null) {
			assignment = assignmentRepository.findOne(assignmentId);

			if (assignment == null) throw new AssignmentNotFoundException(assignmentId);
			uploadedBy = getUploadedBy(assignment);
		} else {
			portfolioItem = portfolioItemRepository.findOne(portfolioId);

			if (portfolioItem == null) throw new PortfolioItemNotFoundException(portfolioId);
			uploadedBy = getUploadedBy(portfolioItem);
		}

		S3Client s3client = new S3Client(props);
		String uploadBucketName = props.getUploadBucket();
		String keyName = createS3FilePrefix(assignmentId, portfolioId, uploadedBy.getId()) + file.getOriginalFilename();
		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.getSize());
			s3client.putObject(new PutObjectRequest(uploadBucketName, keyName, file.getInputStream(), metadata));
		} catch (AmazonServiceException e) {
			log.error(e.getErrorMessage());
			throw new FileInformationAccessFailedException();
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new UploadCouldNotBeConvertedException();
		}


		FileInformation fileInformation = new FileInformation();
		fileInformation.setFileUrl("https://s3.amazonaws.com/" + uploadBucketName + "/" + keyName);
		fileInformation.setUploadedByUser(uploadedBy);
		fileInformation.setCreatedDate(ZonedDateTime.now());
		if (assignment != null) {
			Course course = assignment.getCourse();
			fileInformation.setAssignment(assignment);
			fileInformation.setUser(course.getInstructor());
			fileInformation.setFileType("Assignment");
		} else {
			fileInformation.setPortfolioItem(portfolioItem);
			fileInformation.setUser(portfolioItem.getStudent());
			fileInformation.setFileType("Portfolio");
		}

		return fileInformationTransformer.transform(fileRepository.save(fileInformation));
	}

	public InputStream getUpload(Long fileInformationId) {
		S3Client s3client = new S3Client(props);
		FileInformation fileInformation = fileRepository.findOne(fileInformationId);

		if (fileInformation == null) throw new FileInformationNotFoundException(fileInformationId);

		String bucket = retrieveBucket(fileInformation);
		String key = retrieveKeyName(fileInformation);

		log.debug("Retrieving file at " + bucket);

		try {
			S3Object s3Object = s3client.getObject(new GetObjectRequest(bucket, key));
			InputStream objectData = s3Object.getObjectContent();
			return objectData;
		} catch (AmazonServiceException e) {
			log.error(e.getErrorMessage());
			throw new FileInformationAccessFailedException();
		}
	}

	public void deleteUpload(Long fileInformationId) {
		FileInformation fileInformation = fileRepository.findOne(fileInformationId);

		if (fileInformation == null) throw new FileInformationNotFoundException(fileInformationId);
		deleteUpload(fileInformation);
	}

	public void deleteUpload(FileInformation fileInformation) {
		S3Client s3client = new S3Client(props);
		String bucket = retrieveBucket(fileInformation);
		String key = retrieveKeyName(fileInformation);

		try {
			s3client.deleteObject(new DeleteObjectRequest(bucket, key));
			fileRepository.delete(fileInformation);
		} catch(AmazonServiceException e) {
			log.error(e.getErrorMessage());
			throw new FileInformationAccessFailedException();
		}
	}

	public void deleteUploads(List<FileInformation> files) {
		S3Client s3client = new S3Client(props);

		Map<String, List<String>> bucketToKeyMapping = buildBucketToKeyMapping(files);

		bucketToKeyMapping.forEach((bucket, keys) -> {
			DeleteObjectsRequest request = buildDeleteObjectsRequest(bucket, keys);

			try {
				s3client.deleteObjects(request);
			} catch(AmazonServiceException e) {
				log.error(e.getErrorMessage());
				throw new FileInformationAccessFailedException();
			}
		});
	}

	private Map<String, List<String>> buildBucketToKeyMapping(List<FileInformation> files) {
		List<String> buckets = Lists.transform(files, this::retrieveBucket);
		List<String> keys = Lists.transform(files, this::retrieveKeyName);

		Map<String, List<String>> bucketToKeys = new HashMap<>();

		// Test if there's only one bucket (which will be the typical case)
		Set<String> uniqueBuckets = new HashSet<>(buckets);
		if (uniqueBuckets.size() == 1) {
			bucketToKeys.put(buckets.get(0), keys);
		} else { // There are multiple buckets in the given file set

			for (int index = 0; index < buckets.size(); index++) {
				String bucket = buckets.get(index);
				String key = keys.get(index);
				if (bucketToKeys.containsKey(bucket)) {
					bucketToKeys.get(bucket).add(key);
				} else {
					List<String> list = new ArrayList<>();
					list.add(key);
					bucketToKeys.put(bucket, list);
				}
			}
		}

		return bucketToKeys;
	}

	private DeleteObjectsRequest buildDeleteObjectsRequest(String bucket, List<String> keys) {
		DeleteObjectsRequest request = new DeleteObjectsRequest(bucket);
		List<DeleteObjectsRequest.KeyVersion> keyVersions = new ArrayList<>(keys.size());

		for (String key : keys)
			keyVersions.add(new DeleteObjectsRequest.KeyVersion(key));

		request.setKeys(keyVersions);
		return request;
	}

	private String createS3FilePrefix(Long assignmentId, Long portfolioId, Long uploadedByUserId) {
		// s3 file name is a_[assignmentId]_[filename] for assignments and p_[portfolioId]_[filename] for portfolios
		// and the user uploading the file's hash.
		String assignmentStr = assignmentId != null ? "a_" + assignmentId.toString() + "/" : "";
		String portfolioStr = portfolioId != null ? "p_" + portfolioId.toString() + "/" : "";
		String userStr = uploadedByUserId.toString();
		String prefix = assignmentStr + portfolioStr + userStr + "/";
		return prefix;
	}

	private User getUploadedBy(Assignment assignment) {
		if (SecurityUtils.isAdmin() || SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ORG_ADMIN))
			return assignment.getCourse().getInstructor();
		else
			return userService.getCurrentUser();
	}

	private User getUploadedBy(PortfolioItem portfolioItem) {
		if (SecurityUtils.isAdmin() || SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ORG_ADMIN))
			return portfolioItem.getStudent();
		else
			return userService.getCurrentUser();
	}

	private String retrieveBucket(FileInformation fileInformation) {
		String bucketAndKey = retrieveFullPath(fileInformation);
		int separatingIndex = bucketAndKey.indexOf("/");
		return bucketAndKey.substring(0, separatingIndex);
	}

	private String retrieveKeyName(FileInformation fileInformation) {
		String bucketAndKey = retrieveFullPath(fileInformation);
		int separatingIndex = bucketAndKey.indexOf("/");
		return bucketAndKey.substring(separatingIndex+1);
	}

	private String retrieveFullPath(FileInformation fileInformation) {
		String bucketAndKey;
		try {
			bucketAndKey = new URL(fileInformation.getFileUrl()).getPath()
				.replaceFirst("/", "");
		} catch (MalformedURLException e) {
			log.warn("File URL is malformed, Falling back on prefix and bucket logic");
			Assignment assignment = fileInformation.getAssignment();
			PortfolioItem portfolioItem = fileInformation.getPortfolioItem();
			String bucket = props.getUploadBucket();
			String key = fileInformation.getFileUrl().substring(
				fileInformation.getFileUrl().lastIndexOf("/")+1
			);
			bucketAndKey = bucket + "/" + key;
		}

		return bucketAndKey;
	}
}

