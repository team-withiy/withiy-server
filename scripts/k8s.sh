#!/bin/bash

kubectl delete ns withiy
kubectl create ns withiy
kubectl apply -n withiy -f k8s/prod

# kubectl exec -it -n withiy deploy/backend -- zsh
