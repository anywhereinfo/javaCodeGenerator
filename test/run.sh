#! /bin/bash
usage="$(basename "$0") path-to-jsonFile base-dir-to-template templateName generated-java-file-directory"
while getopts ':hs:' option; do
  case "$option" in
    h) echo "$usage"
       exit
       ;;
  esac
done

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo $DIR
java -jar javaCodeGenerator-0.0.1-SNAPSHOT.jar $DIR/$1 $DIR $2 $DIR/
