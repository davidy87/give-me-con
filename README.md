# 기브미콘 - 기프티콘 거래 플랫폼

## 소개
___

기프티콘 거래 서비스를 제공하는 플랫폼입니다.

API를 제공하는 서버와 화면을 구성하는 프론트엔드로 나누어 개발하였습니다.
* Front-end 확인하기: https://github.com/davidy87/givemecon-front

<br>

## 프로젝트 관심사
___

### 기본사항
* API 제공 (with API 문서)
* 지속적인 코드 정리와 리팩토링
* 테스트 코드


<br>

## 사용 기술
___

* Spring Boot 3.2.3
* MySQL
* JPA
* Redis
* AWS S3
* Google Cloud Vision API (OCR)

<br>

## ERD
___

### [기브미콘 ERD 링크](https://lucid.app/lucidchart/68dce068-b9e4-48c8-aed3-7c88454b2df4/edit?viewport_loc=7523%2C-4495%2C2704%2C1234%2C0_0&invitationId=inv_5a24bfb8-69ef-4cbb-9f75-95202344892b)
![기브미콘 ERD](https://documents.lucid.app/documents/68dce068-b9e4-48c8-aed3-7c88454b2df4/pages/0_0?a=9331&x=7777&y=-4592&w=2245&h=2083&store=1&accept=image%2F*&auth=LCA%207e8fc6172015dceada41816ee836e56f4d010f185397232a5e88ce82f9ebf1c2-ts%3D1713360018)

<br>

## API 문서
___

### [API 문서 확인하기](https://davidy87.github.io/givemecon/api)

<br>

## 요구사항 및 제약사항
___

### Image
* 모든 이미지 파일은 multipart/form-data 요청을 통해 전달된다.
* 전달된 이미지 파일은 AWS S3에 저장하고, S3에 저장된 이미지의 key와 URL, 그리고 실제 이미지 파일의 이름만 DB에 저장한다.

### Voucher
* Voucher 저장 요청 시 description과 caution 필드를 제외한 모든 데이터는 필수이다.
* Voucher 수정 요청 시 모든 필드의 데이터는 생략이 가능하다.
