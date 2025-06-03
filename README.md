Database String Constraints

```
Asset.name         VARCHAR(255)
Asset.description  VARCHAR(1023)
Asset.type         VARCHAR(100)

Clue.content      VARCHAR(255)

CustomMission.context VARCHAR(255)    # In table: custom_mission_contexts

Escape.title      VARCHAR(255)

EscapePlayerMapping.status VARCHAR(255)

Mission.title     VARCHAR(255)
Mission.description VARCHAR(1023)
Mission.type      VARCHAR(255)

MissionOption.description VARCHAR(1023)
MissionOption.conditions  VARCHAR(1023)

MissionPlayerMapping.status VARCHAR(50)      # ENUM MissionStatusEnum

Player.name       VARCHAR(255)
Player.firstName  VARCHAR(255)
Player.mail       VARCHAR(255)
Player.address    VARCHAR(255)
Player.phone      VARCHAR(255)
Player.comment    VARCHAR(1023)

PlayerPreference.preference VARCHAR(50)      # ENUM MissionConditionEnum

Sidekick.name     VARCHAR(255)
Sidekick.firstName VARCHAR(255)
Sidekick.mail     VARCHAR(255)
Sidekick.address  VARCHAR(255)
Sidekick.phone    VARCHAR(255)
Sidekick.comment  VARCHAR(2023)

Universe.title    VARCHAR(255)
``` 

