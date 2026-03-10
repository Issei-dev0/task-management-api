# Task API

JWT認証付きのタスク管理REST APIです。  
Spring Boot を使用して、ユーザーごとのタスク管理機能を実装しています。

このプロジェクトは **バックエンドポートフォリオ**として作成しました。

---

# Live Demo

※ AWSデプロイ後にURLを追加

Swagger UI

```
https://your-domain/swagger-ui/index.html
```

---

# Overview

ユーザー認証付きのタスク管理APIです。  
JWT認証を利用し、ユーザーごとにタスクを管理できるようになっています。

主な特徴

- JWT認証
- Roleベースのアクセス制御
- タスク所有者制御（他ユーザーのタスクアクセス禁止）
- ページング対応
- タスク検索
- FlywayによるDBマイグレーション管理
- DockerによるDB環境管理

---

# Tech Stack

| 技術 | 用途 |
|---|---|
| Java 17 | プログラミング言語 |
| Spring Boot | APIフレームワーク |
| Spring Security | 認証 / 認可 |
| JWT | トークン認証 |
| Spring Data JPA | ORM |
| PostgreSQL | データベース |
| Flyway | DBマイグレーション |
| Docker Compose | DB環境 |
| Swagger (OpenAPI) | APIドキュメント |

---

# Architecture

```
Client
   ↓
Spring Boot API
   ↓
PostgreSQL
```

アプリケーション構造

```
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

DTOを使用して **EntityとAPIレスポンスを分離**しています。

---

# Features

## Authentication

JWTトークンを使用した認証を実装しています。

```
POST /api/auth/login
```

ログイン成功後にJWTを発行し、以降のAPIリクエストで使用します。

```
Authorization: Bearer {token}
```

---

## Task Management

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/tasks | タスク作成 |
| GET | /api/tasks | タスク一覧 |
| GET | /api/tasks/{id} | タスク取得 |
| PUT | /api/tasks/{id} | タスク更新 |
| DELETE | /api/tasks/{id} | タスク削除 |

---

## Pagination

```
GET /api/tasks?page=0&size=20
```

---

## Search

```
GET /api/tasks?q=book
GET /api/tasks?status=TODO
```

---

# Security Design

## JWT Authentication

ログイン時にJWTトークンを発行し、APIアクセス時に検証します。  
APIはステートレスで動作します。

---

## Role Authorization

| Role | 権限 |
|---|---|
| USER | タスク操作 |
| ADMIN | 全ユーザーのタスク管理 |

---

## Owner Authorization

ユーザーは **自分が作成したタスクのみアクセス可能**です。

```
userA → userBのタスクアクセス → 403 Forbidden
```

---

# Database

PostgreSQL を使用しています。

DBスキーマ管理は **Flyway** を使用しています。

```
src/main/resources/db/migration
```

例

```
V1__init.sql
```

---

# Run Locally

## 1. PostgreSQL起動

```
docker compose up -d
```

---

## 2. アプリ起動

```
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 3. Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

# Error Handling

| Status | Description |
|---|---|
| 400 | Validation Error |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Resource Not Found |

---

# Future Improvements

- APIテスト（JUnit / MockMvc）
- Docker Compose に API コンテナ追加
- CI/CD（GitHub Actions）
- フロントエンドUI
- AWSデプロイ

---

# Author

Portfolio Project
