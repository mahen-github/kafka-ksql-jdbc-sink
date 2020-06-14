#!/bin/bash

script_path="$( cd "$(dirname "$0")" ; pwd -P )"

project_root_dir=`echo ${script_path} | awk '{print substr($0, 0, index($0, "shell")-1)}'`

url="http://localhost:8081/subjects"

function registerSchema(){
  local _schema=${project_root_dir}avro/${1}
  local subject=$(echo ${1} | awk -F"." '{print $1}')

  cat ${_schema} | jq 'tostring | {schema: .}' -c | curl -X POST -i -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    --data @- \
    ${url}/${subject}-value/versions
}

declare arr=(
${project_root_dir}/build/generated-main-avro-avsc/com/mahendran/event/Customer.avsc
)

for schema in ${arr[@]}
do
  registerSchema ${schema}
done