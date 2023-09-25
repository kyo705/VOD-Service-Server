# VOD-Service-Server

> 웹으로 VOD 서비스를 제공하는 서버

## VOD Server API

### 파일 메터데이터 초기 생성 요청

```
URL : /api/video/{channel-id}
Method : POST
Content-Type : application/json;charset=utf-8
Request Body : 

{
   "title" : "오펜하이머",
   "description" : "'나는 이제 죽음이요, 세상의 파괴자가 되었다.' 세상을 구하기 위해 세상을 파괴할 지도 모르는 선택을 해야 하는 천재 과학자의 핵개발 프로젝트.",
   "isFree" : false
}

```
### 파일 메타데이터 생성 성공시
```
Status Code : 200 OK
Response Body : 

{
   "videoId" : 150
}
```
