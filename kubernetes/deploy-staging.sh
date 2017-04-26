#!/bin/bash

set -e

# # potential usage to convert to env variables - set values in global vars
# gcloud --quiet config set project $PROJECT_NAME_PRD
# gcloud --quiet config set container/cluster $CLUSTER_NAME_PRD
# gcloud --quiet config set compute/zone ${CLOUDSDK_COMPUTE_ZONE}
# gcloud --quiet container clusters get-credentials $CLUSTER_NAME_PRD
/home/travis/google-cloud-sdk/bin/kubectl apply -f ./kubernetes/kubernetes-app-deploy.yaml # trigger redeployment of Kubernetes Deployment
