# DNS Proxy

[![BSD 3-Clause License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

A lightweight, customizable DNS proxy server written in Java (Spring Boot). It intercepts DNS queries, processes or filters them based on customizable rules, and forwards them to upstream DNS servers as needed.

---

## 🚀 Features

- 🔁 **Query Forwarding**: Forwards DNS requests to upstream resolvers (e.g. 8.8.8.8).
- 🔐 **Selective Filtering**: Block or allow domains based on configurable rules.
- 💡 **Support for A, AAAA, CNAME, HTTPS, SOA records**.
- 📄 **Configurable via application.yml or environment variables**.
- 📊 **Optional metrics and logging support** (debug/error logs separated).
- ☕ Built with **Spring Boot** for easy deployment and extensibility.

---

## 📦 Installation

### 1. Clone the repository

```
git clone https://github.com/sbastianvincent/dnsproxy.git
cd dnsproxy
```
### 2. Build the project
```
./mvnw package
```
### 3. Run the DNS Proxy
```
java -jar ./target/dnsproxy.jar
```

## ⚙️ Configuration

Edit `src/main/resources/application.yml:`
```
dnsproxy:
  port: 53
  upstream-servers:
    - 1.1.1.1
    - 8.8.8.8
```
Or pass as command-line args:
```
java -jar dnsproxy.jar --dnsproxy.port=53 --dnsproxy.upstream-servers=1.1.1.1, 8.8.8.8
```
## 📁 Logging

Logs are separated into three files in the logs/ directory:

* `debug.log` – detailed trace/debug information

* `error.log` – error events

Configure in `logback.xml`.

## 📜 Supported DNS Record Types
| Record Type | Description        | RFC                                                       |
| ----------- | ------------------ | --------------------------------------------------------- |
| `A`         | IPv4 address       | [RFC 1035](https://datatracker.ietf.org/doc/html/rfc1035) |
| `AAAA`      | IPv6 address       | [RFC 3596](https://datatracker.ietf.org/doc/html/rfc3596) |
| `CNAME`     | Canonical name     | [RFC 1035](https://datatracker.ietf.org/doc/html/rfc1035) |
| `HTTPS`     | HTTPS record type  | [RFC 9460](https://datatracker.ietf.org/doc/html/rfc9460) |
| `SOA`       | Start of Authority | [RFC 1035](https://datatracker.ietf.org/doc/html/rfc1035) |

## 📬 Contributing

Pull requests welcome! Please open an issue first if the change is significant.

## 📄 License

BSD 3-Clause License. See [License](https://github.com/sbastianvincent/dnsproxy/LICENSE)

## 👤 Author

Maintained by [Sebastian](https://github.com/sbastianvincent).