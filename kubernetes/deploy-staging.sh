#!/bin/bash

set -e

curl https://sdk.cloud.google.com | bash # fetch latest gcloud cli and install
bash -l /home/travis/google-cloud-sdk/bin/gcloud version
bash -l /home/travis/google-cloud-sdk/bin/gcloud components update kubectl # install kubectl cli
openssl aes-256-cbc -K $encrypted_eaecdb30d04c_key -iv $encrypted_eaecdb30d04c_iv -in OpenGive-Denver-2017-a21ea468d39f.json.enc -out OpenGive-Denver-2017-a21ea468d39f.json -d # decrypt service-account-credentials for gcloud
bash -l /home/travis/google-cloud-sdk/bin/gcloud auth activate-service-account --key-file OpenGive-Denver-2017-a21ea468d39f.json # login to gcloud
bash -l /home/travis/google-cloud-sdk/bin/gcloud container clusters get-credentials staging-cluster --zone us-central1-a --project opengive-denver-2017 #TODO use env variables instead of hard-coding   # get credentials for kubectl from gcloud
# # potential usage to convert to env variables - set values in global vars
# gcloud --quiet config set project $PROJECT_NAME_PRD
# gcloud --quiet config set container/cluster $CLUSTER_NAME_PRD
# gcloud --quiet config set compute/zone ${CLOUDSDK_COMPUTE_ZONE}
# gcloud --quiet container clusters get-credentials $CLUSTER_NAME_PRD
/home/travis/google-cloud-sdk/bin/kubectl apply -f ./kubernetes/kubernetes-app-deploy.yaml # trigger redeployment of Kubernetes Deployment
