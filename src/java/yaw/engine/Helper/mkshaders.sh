#! /bin/sh

PYTHON3=python3

echo "* Making HelperSummit fragment shader *"
$PYTHON3 ../shader/shader_classify.py "yaw.engine.Helper" ./vertShaderHelperSummit.vs

echo "* Making HelperSummit  vertex shader *"
$PYTHON3 ../shader/shader_classify.py "yaw.engine.Helper" ./fragShaderHelperSummit.fs

echo "* Making HelperNormal fragment shader *"
$PYTHON3 ../shader/shader_classify.py "yaw.engine.Helper" ./vertShaderHelperNormal.vs

echo "* Making HelperNormal geometry shader *"
$PYTHON3 ../shader/shader_classify.py "yaw.engine.Helper" ./geoShaderHelperNormal.gs

echo "* Making HelperAxesMesh geometry shader *"
$PYTHON3 ../shader/shader_classify.py "yaw.engine.Helper" ./geoShaderHelperAxesMesh.gs

echo "* Making HelperAxesMesh vertex shader *"
$PYTHON3 ../shader/shader_classify.py "yaw.engine.Helper" ./vertShaderHelperAxesMesh.vs

echo "* Making HelperAxesMesh fragment shader *"
$PYTHON3 ../shader/shader_classify.py "yaw.engine.Helper" ./fragShaderHelperAxesMesh.fs
