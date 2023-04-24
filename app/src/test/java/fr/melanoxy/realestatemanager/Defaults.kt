package fr.melanoxy.realestatemanager

import fr.melanoxy.realestatemanager.domain.estateAgent.EstateAgentEntity

fun getDefaultAgentEntity(agentId: Int) = EstateAgentEntity(
    id = agentId.toLong(),
    firstName = "myFirstName:$agentId",
    lastName = "myLastName:$agentId",
    picUrl = "myPicUrl:$agentId"
)

    fun getDefaultAgentEntitiesAsJson() = """
    [
    {"id":0,"firstName":"myFirstName:0","lastName":"myLastName:0","picUrl":"myPicUrl:0"},
    {"id":1,"firstName":"myFirstName:1","lastName":"myLastName:1","picUrl":"myPicUrl:1"},
    {"id":2,"firstName":"myFirstName:2","lastName":"myLastName:2","picUrl":"myPicUrl:2"}
    ]
""".trimIndent()
