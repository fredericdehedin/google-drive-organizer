# Google Drive File Filter Implementation Plan

## Overview
Implement a filter in [`GoogleDriveFileRepository.java`](../src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveFileRepository.java:1) to return only files (not folders) from Google Drive.

## Current State Analysis

### Current Query (Line 46)
```java
.setQ("'" + driveConfig.checkInFolderId() + "' in parents and trashed=false")
```

This query currently:
- Filters items in the specified check-in folder
- Excludes trashed items
- **Does NOT** filter out folders

### Identified Issue
**Missing File Filter**: No mimeType filter to exclude folders

## Implementation Plan

### Add File Filter
**File**: [`GoogleDriveFileRepository.java`](../src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveFileRepository.java:46)

**Action**: Update the query to include mimeType filter

**Current Query**:
```java
.setQ("'" + driveConfig.checkInFolderId() + "' in parents and trashed=false")
```

**Updated Query**:
```java
.setQ("'" + driveConfig.checkInFolderId() + "' in parents and trashed=false and mimeType='application/vnd.google-apps.file'")
```

### Google Drive API Query Syntax
The query uses Google Drive's search query language:
- `'folderId' in parents` - Items in the specified folder
- `trashed=false` - Exclude trashed items
- `mimeType='application/vnd.google-apps.file'` - Only files (not folders)
- Conditions are combined with `and` operator

## Expected Behavior After Implementation

### Before
- Returns all items in the check-in folder (files AND folders)
- Folders appear in the file list

### After
- Returns only files in the check-in folder
- Folders are filtered out
- Only items with mimeType `application/vnd.google-apps.file` are returned

## Testing Considerations

The existing test [`GoogleDriveFileRepositoryTest.java`](../src/test/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveFileRepositoryTest.java:1) currently only tests error conditions. Consider adding integration tests to verify:
1. Files are returned
2. Folders are excluded
3. The query string is correctly formatted

However, since this is an outbound adapter that calls external Google Drive API, unit testing the query filter would require mocking the Drive service, which may be complex.

## Implementation Steps Summary

1. ✅ Analyze current implementation
2. ✅ Syntax error fixed (completed by user)
3. Update query filter on line 46 to add mimeType condition
4. Verify the code compiles
5. Test manually with actual Google Drive data (if possible)

## Notes

- The mimeType `application/vnd.google-apps.file` is a valid Google Drive API mime type for files
- This filter will exclude:
  - Folders (`application/vnd.google-apps.folder`)
  - Google Docs, Sheets, Slides (native Google Workspace types)
  - Other non-file items
- If you need to include specific Google Workspace types (like Google Docs), the query would need to be adjusted to use `mimeType != 'application/vnd.google-apps.folder'` instead

## References
- Google Drive API v3 Search Query Documentation
- Current implementation: [`GoogleDriveFileRepository.java`](../src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveFileRepository.java:1)
