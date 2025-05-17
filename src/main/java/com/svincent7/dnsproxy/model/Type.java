package com.svincent7.dnsproxy.model;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum Type {
    A((short) 1),
    NS((short) 2),
    MD((short) 3),
    MF((short) 4),
    CNAME((short) 5),
    SOA((short) 6),
    MB((short) 7),
    MG((short) 8),
    MR((short) 9),
    NULL((short) 10),
    WKS((short) 11),
    PTR((short) 12),
    HINFOO((short) 13),
    MINFO((short) 14),
    MX((short) 15),
    TXT((short) 16),
    RP((short) 17),
    AFSDB((short) 18),
    X25((short) 19),
    ISDN((short) 20),
    RT((short) 21),
    NSAP((short) 22),
    NSAP_PTR((short) 23),
    SIG((short) 24),
    KEY((short) 25),
    PX((short) 26),
    GPOS((short) 27),
    AAAA((short) 28),
    LOC((short) 29),
    NXT((short) 30),
    EID((short) 31),
    NIMLOC((short) 32),
    SRV((short) 33),
    ATMA((short) 34),
    NAPTR((short) 35),
    KX((short) 36),
    CERT((short) 37),
    A6((short) 38),
    DNAME((short) 39),
    SINK((short) 40),
    OPT((short) 41),
    APL((short) 42),
    DS((short) 43),
    SSHFP((short) 44),
    IPSECKEY((short) 45),
    RRSIG((short) 46),
    NSEC((short) 47),
    DNSKEY((short) 48),
    DHCID((short) 49),
    NSEC3((short) 50),
    NSEC3PARAM((short) 51),
    TLSA((short) 52),
    SMIMEA((short) 53),
    HIP((short) 55),
    NINFO((short) 56),
    RKEY((short) 57),
    TALINK((short) 58),
    CDS((short) 59),
    CDNSKEY((short) 60),
    OPENPGPKEY((short) 61),
    CSYNC((short) 62),
    ZONEMD((short) 63),
    SVCB((short) 64),
    HTTPS((short) 65),
    SPF((short) 99),
    UINFO((short) 100),
    UID((short) 101),
    GID((short) 102),
    UNSPEC((short) 103),
    NID((short) 104),
    L32((short) 105),
    L64((short) 106),
    LP((short) 107),
    EUI48((short) 108),
    EUI64((short) 109),
    TKEY((short) 249),
    TSIG((short) 250),
    IXFR((short) 251),
    AXFR((short) 252),
    MAILB((short) 253),
    MAILA((short) 254),
    ANY((short) 255),
    URI((short) 256),
    CAA((short) 257),
    AVC((short) 258),
    DOA((short) 259),
    AMTRELAY((short) 260),
    TA((short) 32768),
    DLV((short) 32769);

    private final short value;
    Type(final short value) {
        this.value = value;
    }

    private static final Map<Short, Type> LOOKUP = new HashMap<>();

    static {
        for (Type t : Type.values()) {
            LOOKUP.put(t.getValue(), t);
        }
    }

    public static Type fromValue(final short value) {
        Type result = LOOKUP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Unknown Type: " + value);
        }
        return result;
    }
}
