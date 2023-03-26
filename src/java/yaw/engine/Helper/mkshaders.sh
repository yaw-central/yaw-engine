#! /bin/sh

PYTHON3=python3


echo "* Making shadow fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./vertShaderHelperSummit.fs

echo "* Making shadow fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./fragShaderHelperSummit.fs

echo "* Making shadow fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./vertShaderHelperNormal.fs

echo "* Making shadow fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./geoShaderHelperNormal.fs