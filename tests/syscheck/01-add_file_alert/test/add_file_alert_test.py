def test_agent():
        f = open('/var/ossec/logs/ossec.log', 'r')
        assert "PUT EVENT HERE" in f.read()
        f.close()

def test_manager():
        f = open('/var/ossec/logs/alerts/alerts.log', 'r')
        assert "PUT ALERT HERE" in f.read()
        f.close()
