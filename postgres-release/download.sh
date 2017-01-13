#!/bin/bash
REPO=$1
BRANCH=$2
TARGET=$3
git clone https://github.com/UnionVMS/$REPO.git

cd $REPO
git fetch origin
LATEST_TAG=$(git describe origin/$BRANCH --abbrev=0)
if [[ ! -z "${LATEST_TAG// }" ]];then
	printf "Switching to tag %s" "$LATEST_TAG"
    git checkout tags/$LATEST_TAG -q
fi
cp -r ./* /liquibase/$TARGET/
cd ..
rm -r $REPO