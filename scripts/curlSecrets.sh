#!/bin/bash
echo "Getting secret: 'secret/application' from Vault. Note: you might have to create it first."
echo "See also: https://www.vaultproject.io/api/secret/kv/kv-v2.html"

curl -X "GET" "http://localhost:8200/v1/secret/data/application" -H "X-Vault-Token: root"