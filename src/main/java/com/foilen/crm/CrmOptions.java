/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm;

import org.kohsuke.args4j.Option;

/**
 * The arguments to pass to the crm application.
 */
public class CrmOptions {

    @Option(name = "--debug", usage = "To log everything (default: false)")
    public boolean debug;

    @Option(name = "--configFile", usage = "The config file path (default: none since using the CONFIG_FILE environment variable)")
    public String configFile;

}
