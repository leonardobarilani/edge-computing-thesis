# Tunneling

> Reference: https://linuxize.com/post/how-to-setup-ssh-tunneling/

`ssh` always run from host giampi.

To test local configuration, echo-server has to run remotely and client locally.

To test remote configuration, echo-server has to run locally and client remotely. 

## From Giampi to Mebeim

Local:

	ssh -L [LOCAL_IP:]LOCAL_PORT:DESTINATION:DESTINATION_PORT [USER@]SSH_SERVER

Connect giampi:31340 -> mebeim:31341:

	ssh -L 31340:127.0.0.1:31341 dummy@mebeim.toh.info -p 31337 -N

## From Mebeim to Giampi

Remote:

	ssh -R [REMOTE:]REMOTE_PORT:DESTINATION:DESTINATION_PORT [USER@]SSH_SERVER

Connect mebeim:31338 -> giampi:31339:

	ssh -R 31338:127.0.0.1:31339 dummy@mebeim.toh.info -p 31337 -N

## Test connection

Echo server:

	ncat -e /bin/cat -k -l <31341|31339>

Echo client:

	echo -n 'Line of text' | nc 127.0.0.1 <31340|31338>

## Real scenario

infrastructure.json schema:

* giampi as seen by mebeim: localhost:31338
* mebeim as seen by giampi: localhost:31340

Run both ssh commands locally with 31341 and 31339 replaced with 31112.
