#!/bin/bash -ex 

export PIAZZA_URL=https://piazza.test.geointservices.io
export MANIFEST_FILENAME=manifest.test.yml

./ci/_cf_push.sh
