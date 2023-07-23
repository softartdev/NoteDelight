gh run list --workflow ios.yml --status failure --limit 1000 |
  grep -Eo '[0-9]{10,}' |
  xargs -P 3 -I {} sh -c 'echo "Deleting run {}" && gh run delete {}'
