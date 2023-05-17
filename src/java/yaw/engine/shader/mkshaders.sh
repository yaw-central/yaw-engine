#! /bin/sh

PYTHON3=python3

echo "* Making vertex shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./vertShader.vs

echo "* Making fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./fragShader.fs

echo "* Making shadow vertex shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./shadowVertShader.vs

echo "* Making shadow fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./shadowFragShader.fs

echo "* Making shadow fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./vertShaderHelperSummit.fs

echo "* Making shadow fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./fragShaderHelperSummit.fs

echo "* Making shadow fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./vertShaderHelperNormal.fs

echo "* Making shadow fragment shader *"
$PYTHON3 ./shader_classify.py "yaw.engine.shader" ./geoShaderHelperNormal.fs