# Android to Push Notification Service - Base App
This is the repository of Android to Push Notification Service (Base App).

<a href="https://play.google.com/store/apps/details?id=org.xlfdll.a2pns">
  <img src="https://github.com/xlfdll/xlfdll.github.io/raw/master/images/google-play-badge.png" alt="Get A2PNS (Android) on Google Play Store" height="64">
</a>

[Join iOS TestFlight!](https://testflight.apple.com/join/cbUVu5XI)

## Development
Currently, the first version of A2PNS supports Android to iOS notification deliveries, and it is still under development.

(This might be changed in the future. See Roadmap section below)

### Build
To build Android app:

1. Open the project in Android Studio
2. In package org.xlfdll.a2pns, create a Kotlin code file named ''ExternalData.kt'' and add the following:

```
package org.xlfdll.a2pns

internal object ExternalData {
    // true - Mock Debug Mode. Create payloads but do not send (only log them)
    // false - Production Mode. Will send payloads to push notification services (e.g. Apple's PNS)
    const val MockMode = true
    // true - Debug Mode. Use development server as push notification service (e.g. Apple's PNS) destinations
    // false - Production Mode. Use production server as push notification service destinations
    const val DebugMode = true;
    // Different notification service servers need to use different tokens for authentication
    const val AuthDataURL = <Your Auth Token Server URL: String>
}
```

3. Build the project and test

Please refer to [here](https://github.com/bi119aTe5hXk/A2IPNS/blob/master/README.md) for complete build instructions on other components.

## Roadmap
Below is a rough roadmap for A2PNS project.

Please notice that all descriptions of the future versions are not a guarantee on implementations. Changes may occur.

- 1.0: Android to iOS notification delivery (A2IPNS).
- Future (presumably 2.0): Android to platform X notification delivery
  - Base App will be refactored for easy support on other push notification platforms
  - As a result, A2IPNS (Android side) will become a supported plugin
  - New plugins will be added if needed
  
## License
Currently, A2PNS follows the same licensing options as [A2IPNS](https://github.com/bi119aTe5hXk/A2IPNS).

Refer to [here](https://github.com/bi119aTe5hXk/A2IPNS/blob/master/README.md) for more information on licensing.
