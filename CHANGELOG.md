# Changelog

## 2.3.0 (2021-02-21)

- Reflect API changes:
  - Now `Transaction` has `attachments: List<Attachment>` and `labels: List<Label>` fields - `attachmentIds`
    and `labelIds` are therefore now deprecated
  - A new `getTransaction(id)` API to get a single `Transaction` by id
- Add a `bypassSslChecks` option on `HttpConfiguration` (should never be used in production)
- Update all dependencies to their latest version

## 2.2.0 (2020-12-12)

- Updated all dependencies to their latest version
- Use the new qonto.com domain instead of qonto.eu
- Reflect API changes (a few new fields and objects)

## 2.1.0

Add the `fileName: String` field to the `Attachment` model.

## v2.0.1
Minor comment fix, and dependency updates.

## v2.0.0
Migrated the project to Kotlin Multiplatform:
- Removed Flow based client (was not really useful)
- Added a Callback based client (useful for Swift)
- Tweaked the API a bit to make it work well with Multiplatform

## v1.2.0 (2019-09-14)
Add `pageIndex` to the `Page` object.

## v1.1.1 (2019-09-07)
Updated dependencies (including Kotlin) to their latest versions.

## v1.1.0 (2019-08-18)
Add `totalItems` and `totalPages` to the `Page` object.

## v1.0.0 (2019-08-04)
Initial release.
