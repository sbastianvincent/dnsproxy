package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import com.svincent7.dnsproxy.model.records.parametersvcb.ParameterFactory;
import com.svincent7.dnsproxy.model.records.parametersvcb.ParameterFactoryImpl;
import com.svincent7.dnsproxy.model.records.parametersvcb.ParameterMandatory;
import com.svincent7.dnsproxy.model.records.parametersvcb.ParameterSVCB;
import com.svincent7.dnsproxy.model.records.parametersvcb.ParameterSvcBinding;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.TreeMap;

/**
 * https://datatracker.ietf.org/doc/html/rfc9460.
 * ab cd 81 80 00 01 00 01 00 00 00 00                      ; Header
 * 04 119 119 119 49                                        ; 'www1'
 * 07 101 120 97 109 112 108 101                            ; 'example'
 * 03 99 111 109                                            ; 'com'
 * 00                                                       ; End of QNAME
 * 00 41                                                    ; QTYPE = 65 (HTTPS)
 * 00 01                                                    ; QCLASS = IN
 * c0 0c                                                    ; NAME (pointer to QNAME)
 * 00 41                                                    ; TYPE = 65 (HTTPS)
 * 00 01                                                    ; CLASS = IN
 * 00 00 00 3c                                              ; TTL = 60 seconds
 * 00 19                                                    ; RDLENGTH = 25 bytes
 * 00 00                                                    ; Priority = 0
 * 00 01                                                    ; ALPN length = 1
 * 02 68 32                                                 ; ALPN = "h2"
 * 00 0f                                                    ; SVCB parameter key = port
 * 00 02                                                    ; length
 * 1f 90                                                    ; port 8080 (0x1f90)
 */
@Getter
@ToString(callSuper = true)
public class HTTPSRecord extends Record {
    private final int svcPriority;
    private final Name targetName;
    private final Map<Integer, ParameterSvcBinding> svcParams;

    // Each parameter: 2 bytes key + 2 bytes length + 'length' bytes value
    private static final int PARAM_HEADER_SIZE = 4;

    public HTTPSRecord(final Name name, final Type type, final DNSClass dnsClass, final long ttl, final int length,
                       final MessageInput message)
            throws DNSMessageParseException {
        super(name, type, dnsClass, ttl, length);
        svcParams = new TreeMap<>();
        svcPriority = message.readU16();
        targetName = new Name(message);
        ParameterFactory parameterFactory = new ParameterFactoryImpl();
        while (message.remaining() >= PARAM_HEADER_SIZE) {
            int key = message.readU16();
            int len = message.readU16();
            byte[] value = message.readByteArray(len);
            ParameterSvcBinding param = parameterFactory.getParameterSvcBinding(key);
            param.fromByteArray(value);
            svcParams.put(key, param);
        }
        if (message.remaining() > 0) {
            throw new DNSMessageParseException("Unexpected number of message remaining: " + message.remaining());
        }
        if (!checkMandatoryParams()) {
            throw new DNSMessageParseException("Mandatory parameters are missing");
        }
    }

    public HTTPSRecord(final HTTPSRecord httpsRecord) {
        super(httpsRecord.getName().clone(), httpsRecord.getType(), httpsRecord.getDnsClass(), httpsRecord.getTtl(),
                httpsRecord.getLength());
        svcPriority = httpsRecord.getSvcPriority();
        targetName = httpsRecord.getTargetName().clone();
        svcParams = new TreeMap<>();
        for (Map.Entry<Integer, ParameterSvcBinding> entry : httpsRecord.getSvcParams().entrySet()) {
            this.svcParams.put(entry.getKey(), entry.getValue().clone());
        }
    }

    private boolean checkMandatoryParams() {
        ParameterMandatory param = (ParameterMandatory) svcParams.get(ParameterSVCB.MANDATORY.getValue());
        for (int key : param.getValues()) {
            if (svcParams.get(key) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void rrToByteResponse(final MessageOutput messageOutput) {
        messageOutput.writeU16(svcPriority);
        targetName.toByteResponse(messageOutput);
        for (Map.Entry<Integer, ParameterSvcBinding> entry : svcParams.entrySet()) {
            messageOutput.writeU16(entry.getKey());
            ParameterSvcBinding param = entry.getValue();
            byte[] value = param.toByteArr();
            messageOutput.writeU16(value.length);
            messageOutput.writeByteArray(value);
        }
    }
    @Override
    public Record clone() {
        return new HTTPSRecord(this);
    }
}
