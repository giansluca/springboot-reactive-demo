#!/bin/bash
port=${1:-8080}

curl -H "content-type: application/json" --request POST -d'{"email":"email@random-123"}' http://localhost:${port}/user-profiles
