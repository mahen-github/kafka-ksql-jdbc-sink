#!/bin/sh

/usr/bin/mc config host add myminio http://localhost:9000 $1 $2
for bucketname in $3; do
  /usr/bin/mc mb --region us-west-2 myminio/$bucketname
  /usr/bin/mc policy public myminio/$bucketname
done
exit 0