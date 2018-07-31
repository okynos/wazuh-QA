# test1AddLog.py
def test_agent():
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "Monitoring output of command(10): ls -l /14-check_captured_command/ls_test_dir/ --time-style=+" in f.read()
        f.close()
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "Monitoring full output of command(10): cat /14-check_captured_command/cat_file.txt" in f.read()
        f.close()

def test_manager():
        f = open('/var/ossec/logs/archives/archives.json', 'r')
        assert "ossec: output: 'ls -l /14-check_captured_command/ls_test_dir/ --time-style=+': total 4" in f.read()
        f.close()
        f = open('/var/ossec/logs/archives/archives.json', 'r')
        assert "ossec: output: 'ls -l /14-check_captured_command/ls_test_dir/ --time-style=+': -rw-r--r-- 1 root root 9  cat_file.txt" in f.read()
        f.close()
        f = open('/var/ossec/logs/archives/archives.json', 'r')
        assert "ossec: output: 'cat /14-check_captured_command/cat_file.txt':\\ncat test" in f.read()
        f.close()
