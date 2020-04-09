#!/bin/bash

#############################################################################################
#creates or deletes the table required for the local end to end testing
#############################################################################################

script_path="$( cd "$(dirname "$0")" ; pwd -P )"
source ${script_path}/color.sh

function usage(){
  echo -e "
  ${RED}
    Missing arguments :
    To create table : bash $0 createTable
    To delete table : bash $0 deleteTable
   ${NC}
   "
}

function createTable(){
  local _tableName=$1
  local _attributeDefinitions=$2
  local _keySchema=$3
   aws dynamodb create-table --region us-west-2 \
     --endpoint-url http://localhost:8000 \
     --attribute-definitions ${_attributeDefinitions} \
     --key-schema "${_keySchema}" \
     --table-name ${_tableName} \
     --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1

}

function deleteTable(){
  local tableName=$1
  aws dynamodb delete-table --table-name ${tableName} --region us-west-2 --endpoint-url http://localhost:8000
}

[[ $# -eq 0 ]] && { usage; exit 0; }

if [[ $1 == "createTable" ]]; then
  createTable "one-hop-example" "AttributeName=\"usid\",AttributeType=S" "AttributeName=\"usid\",KeyType=HASH"
fi

if [[ $1 == "deleteTable" ]]; then
  deleteTable one-hop-example
fi
