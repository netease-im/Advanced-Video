import re
import os
import sys

# Read heaer file
APP_KEY_FILE = os.environ['APP_KEY_HEADER_PATH']
if APP_KEY_FILE is None:
	print "APP_KEY_FILE Not found"
	sys.exit(1)
io = open(APP_KEY_FILE, "r+")
text = io.read()

# Throw error if original app key is valid
appKeyStr = re.search('kAppKey\s@\"\S*\"',text).group()
if re.match("kAppKey\s@\"[a-f0-9]{32}\"", appKeyStr) is not None:
	print "A valid app key is submitted!"
	sys.exit(1)

kAppKey = os.environ['APP_KEY']
if kAppKey is None: 
	print "kAppKey not found in secrets"
	sys.exit(1)

# Stub valid key and write back
ret = re.sub('kAppKey\s@\"\S*\"', 'kAppKey @\"' + kAppKey + '\"', text)
io.seek(0)
io.write(ret)
io.truncate()

# Stream URL
streamUrlStr = re.search('kStreamURL\s@\"\S*\"',text).group()
if streamUrlStr.startswith('rtmp://'):
	print "A valid stream url is submitted!"
	sys.exit(1)

kStreamURL = os.environ['STREAM_URL']
if kStreamURL is None: 
	print "kStreamURL not found in secrets"
	sys.exit(1)

# Stub valid key and write back
ret = re.sub('kStreamURL\s@\"\S*\"', 'kStreamURL @\"' + kStreamURL + '\"', text)
io.seek(0)
io.write(ret)
io.truncate()

io.close()