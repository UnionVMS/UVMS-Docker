# UnionVMSparameters.py:
appServerName = "localhost"
dbServerName = "db71u"
hostdbServerName = "localhost"
httpNAFRequestString = "http://" + appServerName + ":28080/naf/rest/message/"
httpUnionVMSurlString = "http://" + appServerName + ":28080/unionvms/"
connectToDatabaseString = "dbname='"+ dbServerName + "' user='postgres' host='" + hostdbServerName + "' password='postgres'" + " port='25432'"
browserTimeout = 15
browserWaitAfterClick = 4