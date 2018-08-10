containers=$(docker ps -q)
ctr=0

for container in $containers
do
  ctr=$((ctr + `docker exec $container /bin/bash -c "cat /output.txt | wc -l"` )) 
done

echo "Total: $ctr"
