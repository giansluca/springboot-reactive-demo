#!/bin/bash
port=${1:-8080}

make_update() {
  echo "every some creation is time for an update, candidate id is: $1"
  email=$(curl -s -H "content-type: application/json" --request PUT -d'{"email":"'${i}'-updated-email@cool.org"}' http://localhost:${port}/user-profiles/$id | \
    python -c "import sys, json; print(json.load(sys.stdin)['emailAddress'])")

  echo "updated email: $email"
  printf "\n"
  sleep 5;
}

for i in {1..1000};
  do (
    id=$(curl -s -H "content-type: application/json" --request POST -d'{"email":"'${i}'-email@cool.org"}' http://localhost:${port}/user-profiles | \
      python -c "import sys, json; print(json.load(sys.stdin)['id'])")

    echo $id

    loop=$(($i%5))
    if [ $loop -eq 0 ]
    then
      printf "\n"
      make_update $id &
    fi
  );
  sleep 2;
done;