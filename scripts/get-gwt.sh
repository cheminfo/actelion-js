#/bin/bash

wget -q https://github.com/gwtproject/gwt/releases/download/2.10.0/gwt-2.10.0.zip -O gwt.zip
unzip -q gwt.zip
mv gwt-2* gwt
echo '{"gwt": "./gwt"}' > config.json
