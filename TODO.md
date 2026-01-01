# TODO: Fix Jackson and SQLite Errors

## Completed Tasks
- [x] Add Jackson JSR310 dependency to pom.xml for LocalDateTime serialization support
- [x] Import JavaTimeModule in WebSocketHandler.java
- [x] Initialize ObjectMapper with JavaTimeModule in WebSocketHandler constructor
- [x] Fix Db.java saveMessage method to use SQLite's last_insert_rowid() instead of getGeneratedKeys()
- [x] Fix Db.java createUser method to use SQLite's last_insert_rowid() instead of getGeneratedKeys()

## Followup Steps
- [ ] Build the project with Maven to ensure no compilation errors
- [ ] Run the application and test WebSocket functionality to verify fixes
- [ ] Verify that messages with LocalDateTime are properly serialized/deserialized
- [ ] Verify that message and user creation works without SQLFeatureNotSupportedException
