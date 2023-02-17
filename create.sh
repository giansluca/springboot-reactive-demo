#!/bin/bash
port=${1:-8080}

curl -H "content-type: application/json" --request POST -d'{"email":"random"}' http://localhost:${port}/profiles 
