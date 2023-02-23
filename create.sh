#!/bin/bash
port=${1:-8080}

for i in {1..1000};
  do (
    curl -H "content-type: application/json" --request POST -d'{"email":"'${i}'-email@cool.org"}' http://localhost:${port}/user-profiles
  );
  sleep 2;
  printf "\n"
done;