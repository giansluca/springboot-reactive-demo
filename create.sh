#!/bin/bash
port=${1:-8080}

for i in {1..1000};
  do (
    curl -H "content-type: application/json" --request POST -d'{"email":"email@random-123"}' http://localhost:${port}/user-profiles
  );
  sleep 2;
  printf "\n"
done;