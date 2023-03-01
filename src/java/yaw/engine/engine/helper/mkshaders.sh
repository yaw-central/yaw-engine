#! /bin/sh

PYTHON3=python3

echo "* Making skybox vertex shader *"
$PYTHON3 ../shader/shader_classify.py "yaw.engine.helper" ./originHelperVertShader.vs

echo "* Making skybox fragment shader *"
$PYTHON3 ../shader/shader_classify.py "yaw.engine.helper" ./originHelperFragShader.fs

