{"programModules":{"EXT002MI":{"program":"EXT002MI","triggers":{},"transactions":{"GetSLMRowNum":{"sourceUuid":"58a3f14f-9385-4f47-b7e8-d8c0d355de6a","name":"GetSLMRowNum","program":"EXT002MI","description":"Get Next Row Number","active":true,"multi":false,"modified":1673534791857,"modifiedBy":"ARTWRI","outputFields":[{"name":"ROWN","description":"Row Number","length":15,"mandatory":false,"type":"N"}],"inputFields":[{"name":"CONO","description":"Company","length":3,"mandatory":true,"type":"N"}],"utilities":[]}},"batches":{},"advancedPrograms":{}}},"utilities":{},"sources":{"58a3f14f-9385-4f47-b7e8-d8c0d355de6a":{"uuid":"58a3f14f-9385-4f47-b7e8-d8c0d355de6a","updated":1706611891539,"updatedBy":"ARTWRI","created":1673534661460,"createdBy":"ARTWRI","apiVersion":"0.21","beVersion":"16.0.0.20231123104741.4","language":"GROOVY","codeHash":"2C02C69945F51753CF80E7E422689296C0F5EE0CDD72E9472DA2755D0EB728BB","code":"LyoqDQogKiAgQnVzaW5lc3MgRW5naW5lIEV4dGVuc2lvbg0KICovDQogLyoqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioNCiBFeHRlbnNpb24gTmFtZTogR2V0U0xNUm93TnVtDQogVHlwZSA6IEV4dGVuZE0zVHJhbnNhY3Rpb24NCiBTY3JpcHQgQXV0aG9yOiBBcnVuIFRpd2FyaQ0KIERhdGU6IDIwMjItMDMtMjUNCiAgDQogRGVzY3JpcHRpb246DQogICAgICAgRmV0Y2ggdGhlIGxhc3Qgcm93IG51bWJlciBmcm9tIEVYVFNMTSB0YWJsZQ0KICAgICAgICAgIA0KIFJldmlzaW9uIEhpc3Rvcnk6DQogTmFtZSAgICAgICAgICAgICAgICAgICAgRGF0ZSAgICAgICAgICAgICBWZXJzaW9uICAgICAgICAgIERlc2NyaXB0aW9uIG9mIENoYW5nZXMNCiBBcnVuIFRpd2FyaSAgICAgICAgICAgIDIwMjItMDMtMjUgICAgICAgICAgMS4wICAgICAgICAgICAgICBJbml0aWFsIFZlcnNpb24NCiAqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKiovDQogDQpwdWJsaWMgY2xhc3MgR2V0U0xNUm93TnVtIGV4dGVuZHMgRXh0ZW5kTTNUcmFuc2FjdGlvbiB7DQogIHByaXZhdGUgZmluYWwgTUlBUEkgbWkNCiAgcHJpdmF0ZSBmaW5hbCBEYXRhYmFzZUFQSSBkYXRhYmFzZQ0KICBwcml2YXRlIGZpbmFsIFByb2dyYW1BUEkgcHJvZ3JhbQ0KDQogIHB1YmxpYyBHZXRTTE1Sb3dOdW0oTUlBUEkgbWksIERhdGFiYXNlQVBJIGRhdGFiYXNlLCBVdGlsaXR5QVBJIHV0aWxpdHksIFByb2dyYW1BUEkgcHJvZ3JhbSkgew0KICAgIHRoaXMubWkgPSBtaQ0KICAgIHRoaXMuZGF0YWJhc2UgPSBkYXRhYmFzZQ0KICAgIHRoaXMucHJvZ3JhbSA9IHByb2dyYW0NCiAgfQ0KDQogIHB1YmxpYyB2b2lkIG1haW4oKSB7DQogICAgaW50IGNvbXBhbnkgPSBtaS5pbi5nZXQoIkNPTk8iKQ0KICAgIFN0cmluZyBzZXEgPSAiMCINCg0KICAgIC8vc2VsZWN0IEVYVFNMTQ0KICAgIERCQWN0aW9uIEVYVFNMTV9xdWVyeSA9IGRhdGFiYXNlLnRhYmxlKCJFWFRTTE0iKS5pbmRleCgiMTAiKS5zZWxlY3Rpb24oIkVYUk9XTiIpLmJ1aWxkKCkNCiAgICBEQkNvbnRhaW5lciBFWFRTTE1fY29udGFpbmVyID0gRVhUU0xNX3F1ZXJ5LmdldENvbnRhaW5lcigpDQogICAgRVhUU0xNX2NvbnRhaW5lci5zZXQoIkVYQ09OTyIsIGNvbXBhbnkpDQoNCiAgICBDbG9zdXJlPD8+IHByb2Nlc3NSZWNvcmQgPSB7IERCQ29udGFpbmVyIHJlY29yZCAtPg0KICAgICAgc2VxID0gcmVjb3JkLmdldExvbmcoIkVYUk9XTiIpLnRvU3RyaW5nKCkNCiAgICB9DQoNCiAgICBpZighRVhUU0xNX3F1ZXJ5LnJlYWRBbGwoRVhUU0xNX2NvbnRhaW5lciwxLDEsIHByb2Nlc3NSZWNvcmQpKSB7DQogICAgICBtaS5lcnJvcigiVGhlIHJlY29yZCBkb2VzIG5vdCBleGlzdCIpDQogICAgICByZXR1cm4NCiAgICB9DQogICAgbWkub3V0RGF0YS5wdXQoIlJPV04iLHNlcSkNCiAgICBtaS53cml0ZSgpDQogIH0NCn0="}}}