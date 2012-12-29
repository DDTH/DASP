namespace java ddth.dasp.thrift.api

service DaspJsonApi {
 	string callApi(1: string moduleName, 2: string functionName, 3: string jsonEncodedInput, 4: string authKey)
}
