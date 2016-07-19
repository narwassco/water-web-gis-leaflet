ビルドの方法

1．python2.7をCドライブ直下にインストールして下さい。
※Cドライブ直下以外にインストールした場合はbuild.pyファイルの一番上のパスを変更してください。

2.compile.batを実行する。
基本的にディレクトリ内のcompile.batを実行すればよいですが、
個別にコンパイルする場合は2ではなく3から行ってください。。

3. コマンドプロンプトを立ち上げる

4. buildディレクトリに移動

5. 以下のコマンドを実行します

（１）ファイルをくっつけるだけ
python build.py -c none full gis-none.js

（２）余分な服す行のコメントを除去してファイルをくっつける
python build.py -c minimize full gis-minimize.js

（３）空白やコメントを除去してファイルをくっつける
python build.py -c jsmin full gis-jsmin.js

コンパイル後の3つのファイルの中で使用したいファイルについて、
buildフォルダの一つ上の階層のgis-jsmin.jsと置き換えてください。
