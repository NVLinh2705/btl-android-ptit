# Some notes in bug-fixing process
## 1. BuildConfig in SupabaseClient: UNRESOLVED REFERENCE
- Cause: BuildConfig class is generated dynamically during the build process, and the IDE sometimes fails to index it immediately
- Solution: clean build the project, use `./gradlew clean :app:assembleDebug`
- other ways: 
  - File > Sync Project with Gradle Files.
  - Build > Assemble Project
  - File > Invalidate caches/ restart