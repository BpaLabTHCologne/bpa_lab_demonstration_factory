# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.1/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.1/gradle-plugin/packaging-oci-image.html)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/4.1.1/reference/data/nosql.html#data.nosql.mongodb)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

"bpalab/ftfactory/f/i/state/hbw",

    payload_hbw = '{{"ts":"{}","station":"hbw","code":{},"active":{}}}'.format(timestamp_utcnow(), _code, _active)

"bpalab/ftfactory/f/i/state/vgr",

    payload = '{{"ts":"{}","station":"vgr","code":{},"active":{},"target":"{}"}}'.format(timestamp_utcnow(), _code,
_active, _target)

"bpalab/ftfactory/f/i/state/mpo", 

    payload_mpo = '{{"ts":"{}","station":"mpo","code":{},"active":{}}}'.format(timestamp_utcnow(), _code, _active)

"bpalab/ftfactory/f/i/state/sld",

    payload_sld = '{{"ts":"{}","station":"sld","code":{},"active":{}}}'.format(timestamp_utcnow(), _code, _active)


"bpalab/ftfactory/f/i/order"

    payload_order = '{{"ts":"{}","state":"{}","type":"{}", "processOrderReference":"{}"}}'.format(
    timestamp_utcnow(), state, type2, _processOrderReference)

"bpalab/ftfactory/i/bme680",

    payload_bme680 = '{{"ts":"{}","t":{:.1f},"rt":{:.1f},"h":{:.1f},"rh":{:.1f},"p":{:.1f},"iaq":{},"aq":{},"gr":{}}}'.format(
    timestamp_utcnow(), (TXT_SSC_M_I2C_1_environment_sensor.get_temperature()) - 4, 0,
    TXT_SSC_M_I2C_1_environment_sensor.get_humidity(), 0, TXT_SSC_M_I2C_1_environment_sensor.get_pressure(),
    TXT_SSC_M_I2C_1_environment_sensor.get_indoor_air_quality_as_number(),
    TXT_SSC_M_I2C_1_environment_sensor.get_accuracy(), 0)

"bpalab/ftfactory/i/ldr",

    payload_ldr = '{{"ts":"{}", "br":{:.1f}, "ldr":{}}}'.format(timestamp_utcnow(), round((65000 - TXT_SSC_M_I3_photo_resistor.get_resistance()) / 650, 1),
    TXT_SSC_M_I3_photo_resistor.get_resistance())

