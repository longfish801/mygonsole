# mygonsole

[TOC levels=2-6]

## 概要

　Groovy Consoleをカスタマイズしました。

　個人が学習のために開発したものです。
　故障対応や問合せ回答などのサポートはしていません。

## 特徴

* タブを入力しても半角スペースに変換されません。
* 起動時に tmp.groovyを開きます。

## 必要な環境

* Java8

## クイックスタート

　カスタマイズされたGroovy Consoleを起動するには、以下を実施してください。

1. [mygonsoleリポジトリ](https://github.com/longfish801/mygonsole)をダウンロードしてください。
2. gradleがインストールされているならば  `gradle` コマンドを、そうでなければ `gradlw` コマンドを実行してください。
3. mygonsole.exeの生成に成功したならば、それを実行してください。

## Groovydoc

* [Groovydoc](groovydoc/)

## 補足

- 動作を確認したいライブラリを追加したい場合
	- build.gradleの dependenciesにライブラリを追記し、実行ファイルを再作成してください。
- 利用する Groovyのバージョンを変更したい場合
	- build.gradleの dependencies内の Groovyのバージョン（nameキーの値が "groovy-all"）を修正してから、実行ファイルを再作成してください。
- 起動時に開くファイルを変更したい場合
	- mygonsole.l4j.iniのシステムプロパティ「init.script」の指定値を編集してください。
- 起動時にファイルを開きたくない場合
	- mygonsole.l4j.iniのシステムプロパティ「init.script」の指定を削除してください。
