//
//  ILScheduleXMLParser.m
//  Inner Life
//
//  Created by Matthew Bates on 13/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILScheduleXMLParser.h"

@implementation ILScheduleXMLParser

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict
{
    if ([elementName isEqualToString:@"session"])
    {
        _currentSession = [[NSMutableArray alloc] init];
    }
    else if ([elementName isEqualToString:@"section"])
    {
        NSInteger sectionID = [[attributeDict objectForKey:@"id"] integerValue];
        [_currentSession addObject:[NSNumber numberWithInteger:sectionID]];
    }
    
    
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if ([elementName isEqualToString:@"session"])
    {
        [_schedule add:_currentSession];
    }
}

@end
