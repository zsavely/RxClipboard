#!/bin/bash

mv /home/travis/build/zsavely/RxClipboard/rxclipboard/build/coverage_first.ec /home/travis/build/zsavely/RxClipboard/rxclipboard/build/outputs/code-coverage/connected/coverage_first.ec

# Try to trick codecov that we have both release and debug code coverage.
mkdir -p /home/travis/build/zsavely/RxClipboard/rxclipboard/build/reports/coverage/release/
mv /home/travis/build/zsavely/RxClipboard/rxclipboard/build/report_first.xml /home/travis/build/zsavely/RxClipboard/rxclipboard/build/reports/coverage/release/report.xml
