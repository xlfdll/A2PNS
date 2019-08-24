# Data Policy
This file documents data policy used for A2PNS app.

## What kind of information is involved?
A2PNS requests the following permissions and data:

- **Internet access**: to request (download) authentication tokens of push notification services (e.g. Apple's APNS) from bi119aTe5hXk's server for further use
- **Camera**: to pair target device (e.g. iOS device) using QR code with specific data structure
- **"Listening" to all notifications in Android system**: to send them to paired target device via push notification services (e.g. Apple's APNS)

All the data structures can be found in [A2PNS](https://github.com/xlfdll/A2PNS) and [A2IPNS](https://github.com/bi119aTe5hXk/A2IPNS) repositories.

## How and what to do with this information?
All above information is only used within the range of user's own devices, which means:

- As developers (namely Xlfdll and bi119aTe5hXk), we do not collect and store any user data, including all the data listed above, nor do we handle them personally and directly
- All the notifications are sent directly to user devices via push notification services (e.g. Apple's APNS). We do not perform any additional processing on them, except packaging them to standard payloads according to the definitions provided by push notification services (e.g. Apple's APNS)
- Except sensitive certificates and keys used by developers for setting up authentication token servers and push notification services, all data and behaviors A2PNS have can be checked by looking at the source code in [A2PNS](https://github.com/xlfdll/A2PNS) and [A2IPNS](https://github.com/bi119aTe5hXk/A2IPNS) repositories

## How to contact us?
One can submit issues [here](https://github.com/bi119aTe5hXk/A2IPNS).
