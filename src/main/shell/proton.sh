#!/bin/bash

TOKEN=""

function usage(){
  echo -e "
  ${RED}
    Missing arguments :
    To create sink : bash $0 create
    To update table : bash $0 update
   ${NC}
   "
}

function getToken(){
local encoded_auth=$(echo -n "0oa6suecyyLgGc8DC2p7:O9Sil3Pxog8fhPSlp7A24EQdtg-aKTNo3sAXDHBy" |
base64)

local _cmd="curl -s --request POST \
  --url https://nordstrom.okta.com/oauth2/aus3uq181tBmOyFqZ2p7/v1/token \
  --header 'accept: application/json' \
  --header 'authorization: Basic ${encoded_auth}' \
  --header 'cache-control: no-cache' \
  --header 'content-type: application/x-www-form-urlencoded' \
  --data 'grant_type=client_credentials&scope=full:access'"

  local response=`eval ${_cmd}`
  local _retCode=$?

  if [[ ${_retCode} -eq 0 ]]; then
     local error=`echo ${response} | jq -r '.'error`
     [[ ${error} != null ]] && { echo -e "\t\nFailed to curl"; echo -e "\t\n${response} "; exit 1; }
  fi

  TOKEN=`echo ${response} | jq -r ."access_token"`
}

function createSink(){
  getToken

  [[ -z ${TOKEN} ]] && { echo "No bearer token available"; exit 1; }

  [[ -z $1 ]] && { echo "Missing input json "; exit 0; }
  local _file=$1

  local _cmd="curl -X POST https://proton.vip.nordstrom.com/api/kafka-clusters/brook/kafka-connectors \
    -H 'Content-Type: application/json' \
    -H 'Accept: application/json' \
    -H 'Authorization: Bearer $TOKEN' \
    -d @${_file}"
    echo ${_cmd}
    eval ${_cmd}
}

[[ $# -eq 0 ]] && { usage; exit 0; }

createSink $1