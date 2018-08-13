# test1AddLog.py
def test_agent():
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "Monitoring output of command(10): du -a /check_captured_command/ls_test_dir/" in f.read()
        f.close()
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "Monitoring full output of command(10): cat /check_captured_command/cat_file.txt" in f.read()
        f.close()

def test_manager():
        f = open('/var/ossec/logs/archives/archives.json', 'r')
        assert "ossec: output: 'du -a /check_captured_command/ls_test_dir/': 4\\t/14-check_captured_command/ls_test_dir/cat_file.txt" in f.read()
        f.close()
        f = open('/var/ossec/logs/archives/archives.json', 'r')
        assert "ossec: output: 'du -a /check_captured_command/ls_test_dir/': 4\\t/14-check_captured_command/ls_test_dir/" in f.read()
        f.close()
        f = open('/var/ossec/logs/archives/archives.json', 'r')
        assert "ossec: output: 'cat /check_captured_command/cat_file.txt':\\ncat test" in f.read()
        f.close()
