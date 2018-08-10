containers=$(docker ps -q)

echo "Preparing dockers"
sleep 5

for container in $containers
do
  echo "clean active-responses.log"
  docker exec $container /bin/bash -c 'echo -ne "" > /var/ossec/logs/active-responses.log'
  echo "clean output file"
  docker exec $container /bin/bash -c "rm -rf /output.txt"
  echo "cp generator in $container"
  docker cp ossec_alerts_generator.py $container:/
  echo "cp log in $container"
  docker cp logs_test.log $container:/ 
done

echo "Launch test"
sleep 2
for container in $containers
do
  echo "exec generator in $container"
  docker exec $container /bin/bash -c "python3 /ossec_alerts_generator.py > test.txt" & 
done

