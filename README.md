# オニオンアーキテクチャサンプル（書籍管理システム）

## 技術スタック
- 言語/ランタイム: Kotlin 2.2.x / JDK 21
- フレームワーク: Spring Boot 4.0.x
- ビルド: Gradle（Groovy DSL）
- ORM: jOOQ
- マイグレーション: Flyway
- テスト: JUnit 5
- データベース: PostgreSQL

## アーキテクチャ概要

[architecture.md](docs/architecture.md)に記載

## コーディングルール
[coding-rule.md](docs/coding-rule.md)に記載

## 起動・テスト方法
- 前提: JDK 21、Docker (PostgreSQL を起動する場合)
- テスト: `./gradlew test`
- DB 起動: `docker compose up -d postgres`
- アプリ起動: `./gradlew bootRun`（DB が起動していること）
- Swagger UI: アプリ起動後、`http://localhost:8080/swagger-ui/index.html` にアクセス

## TODO
- フォーマッター・リンターの導入
- 各層ごとのマルチプロジェクト構成の検討
- jOOQのKotlinコード自動生成設定