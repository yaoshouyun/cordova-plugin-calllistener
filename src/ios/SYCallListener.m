#import <Cordova/CDVPlugin.h>
#import "SYCallListener.h"

@implementation CallRecord
@synthesize duration, startDate, endDate, callID;
- (CallRecord*)init
{
    self = (CallRecord*)[super init];
    if (self) {
        self.duration = 0;
        self.startDate = nil;
        self.endDate = nil;
        self.callID = nil;
    }
    return self;
}
@end

@implementation SYCallListener

- (void)addListener:(CDVInvokedUrlCommand*)command
{
    NSLog(@"Added Call Listener.");
    
    __block CDVPluginResult* pluginResult = nil;    
    self.objCallCenter = [[CTCallCenter alloc] init];
    __block CDVPlugin *blockSelf = self;
    self.objCallCenter.callEventHandler = ^(CTCall* call) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[self parseStatus:call]];
        [pluginResult setKeepCallbackAsBool:true];
        [blockSelf.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    };  
}


- (int)parseStatus:(CTCall *)call {
    NSString *callInfo = call.callState;
    NSLog(@"callID:%@",call.callID);
    // 1空闲、2响铃、3通话
    if([callInfo isEqualToString: CTCallStateDialing]) {
        //The call state, before connection is established, when the user initiates the call.
        NSLog(@"Call is dailing");
        [self clearRecord];
        self.record.callID = call.callID;
        return 2;
    } else if([callInfo isEqualToString: CTCallStateIncoming]) {
        //The call state, before connection is established, when a call is incoming but not yet answered by the user.
        NSLog(@"Call is Coming");
        return 4;
    } else if([callInfo isEqualToString: CTCallStateConnected]) {
        //The call state when the call is fully established for all parties involved.
        NSLog(@"Call Connected");
        if ( [self.record.callID isEqualToString: call.callID]) {
            self.record.startDate = [NSDate date];
        }
        return 3;
    } else if([callInfo isEqualToString: CTCallStateDisconnected]) {
        //The call state Ended.
        NSLog(@"Call Ended");
        if ( [self.record.callID isEqualToString: call.callID]) {
            self.record.endDate = [NSDate date];
            self.record.duration = [self.record.endDate timeIntervalSinceDate:self.record.startDate];
        }
        return 6;
    } else {
        return 1;
    }
}

- (void)getCallInfo:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    
    if (self.record.duration > 0) {
        NSDictionary *recordDict = [[NSDictionary alloc]
                                    initWithObjectsAndKeys:
                                    @(self.record.duration), @"duration",
                                    [self format:self.record.startDate], @"start",
                                    [self format:self.record.endDate],@"end",
                                    nil];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:recordDict];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (CallRecord *)record {
    if(!_record) _record = [[CallRecord alloc] init];
    return _record;
}

- (void)clearRecord {
    _record = nil;
}

- (NSString *)format:(NSDate *)date
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *stringDate = [dateFormatter stringFromDate:date];
    return stringDate;
}

@end
