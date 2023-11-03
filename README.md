## 로컬에 스프링 띄우기

최상위 디렉토리(`/busan`)에서

```bash
sudo docker compose up -d
docker build -t busan/app .
docker run -p 80:8080 
```
