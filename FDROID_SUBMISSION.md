# F-Droid Submission Guide

This guide will help you submit Belote Scoreboard to F-Droid.

## Prerequisites

✅ All requirements are already completed in this repository:
- [x] GPL-3.0-or-later license (see [LICENSE](LICENSE))
- [x] No proprietary dependencies (verified)
- [x] F-Droid metadata file ([fdroid.yml](fdroid.yml))
- [x] Fastlane metadata for app description ([fastlane/metadata/](fastlane/metadata/))
- [x] Source code on GitHub at https://github.com/yx7ex/BeloteScoreboard

## Step-by-Step Submission Process

### 1. Create a Git Tag for v1.0

Before submitting, you must tag your release:

```bash
git tag -a v1.0 -m "Version 1.0 - Initial release"
git push origin v1.0
```

**Important:** The fdroid.yml file references `commit: "v1.0"`, so this tag must exist.

### 2. Create a GitHub Release (Optional but Recommended)

1. Go to https://github.com/yx7ex/BeloteScoreboard/releases
2. Click "Create a new release"
3. Choose the `v1.0` tag
4. Title: "v1.0 - Initial Release"
5. Description: Copy from [fastlane/metadata/android/en-US/changelogs/1.txt](fastlane/metadata/android/en-US/changelogs/1.txt)
6. Click "Publish release"

### 3. Submit to F-Droid

There are two ways to submit your app to F-Droid:

#### Option A: Request For Packaging (RFP) - Recommended for First-Time Publishers

1. Go to the F-Droid Data repository: https://gitlab.com/fdroid/fdroiddata
2. Create a new issue with the "Request For Packaging" template
3. Fill in the details:
   - **App Name:** Belote Scoreboard
   - **Package ID:** com.github.yx7ex.belotescoreboard
   - **Source Code:** https://github.com/yx7ex/BeloteScoreboard
   - **License:** GPL-3.0-or-later
   - **Categories:** Games
   - **Description:** Paste from [fastlane/metadata/android/en-US/full_description.txt](fastlane/metadata/android/en-US/full_description.txt)

4. The F-Droid team will review and may add your app

#### Option B: Submit Metadata Directly via Merge Request

This is faster but requires familiarity with F-Droid's process:

1. Fork the F-Droid Data repository: https://gitlab.com/fdroid/fdroiddata
2. Clone your fork locally
3. Copy your [fdroid.yml](fdroid.yml) to the `metadata/` directory in fdroiddata:
   ```bash
   cp fdroid.yml /path/to/fdroiddata/metadata/com.github.yx7ex.belotescoreboard.yml
   ```
4. Commit and push to your fork:
   ```bash
   git add metadata/com.github.yx7ex.belotescoreboard.yml
   git commit -m "New app: Belote Scoreboard"
   git push origin main
   ```
5. Create a Merge Request to fdroiddata
6. Wait for F-Droid maintainers to review

### 4. What Happens Next?

- F-Droid maintainers will review your submission
- They may request changes or ask questions
- Once approved, F-Droid will build your app on their infrastructure
- Your app will appear in the F-Droid repository within 1-3 days after approval
- Updates are checked automatically when you create new git tags

## Updating Your App

When you release a new version:

1. Update `versionCode` and `versionName` in [app/build.gradle.kts](app/build.gradle.kts)
2. Create a new changelog file: `fastlane/metadata/android/en-US/changelogs/<versionCode>.txt`
3. Add a new build entry to [fdroid.yml](fdroid.yml):
   ```yaml
   - versionName: "1.1"
     versionCode: 2
     commit: "v1.1"
     subdir: app
     gradle:
       - release
   ```
4. Commit changes, create git tag, and push:
   ```bash
   git tag -a v1.1 -m "Version 1.1"
   git push origin v1.1
   ```
5. F-Droid will automatically detect the update and build it

## Troubleshooting

### Build Fails on F-Droid

- Check the F-Droid issue tracker for your app
- Common issues:
  - Missing git tag (ensure `v1.0` exists)
  - Incorrect subdir path in fdroid.yml
  - Gradle version compatibility

### App Not Appearing

- It can take 1-3 days after approval
- Check https://f-droid.org/packages/com.github.yx7ex.belotescoreboard/
- Monitor the F-Droid Data merge request or issue

### Need Help?

- F-Droid Documentation: https://f-droid.org/docs/
- F-Droid Forum: https://forum.f-droid.org/
- F-Droid Matrix Chat: #fdroid:f-droid.org

## Verification

Before submitting, verify your metadata is correct:

```bash
# Check that all metadata files exist
ls -la fastlane/metadata/android/en-US/
# Should show: full_description.txt, short_description.txt, title.txt, changelogs/

# Verify fdroid.yml syntax
cat fdroid.yml

# Ensure git tag exists
git tag -l | grep v1.0
```

## Additional Resources

- [F-Droid Inclusion Policy](https://f-droid.org/docs/Inclusion_Policy/)
- [F-Droid Build Metadata Reference](https://f-droid.org/docs/Build_Metadata_Reference/)
- [F-Droid FAQ for App Developers](https://f-droid.org/docs/FAQ_-_App_Developers/)
