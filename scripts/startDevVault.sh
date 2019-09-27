#!/bin/bash
echo "Starting Hashicorp Vault for Development purposes. DON'T use for production purposes!"
docker container rm dev-vault
docker run --cap-add=IPC_LOCK -d --name=dev-vault vault
docker container ls